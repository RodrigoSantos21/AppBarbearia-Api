using MediatR;
using AppBarbearia.Application.Common;
using AppBarbearia.Domain.Enums;
using AppBarbearia.Domain.Interfaces;
using System.Globalization;

namespace AppBarbearia.Application.Queries.Appointments;

public sealed class GetMonthlyRevenueReportQueryHandler(IAppointmentRepository appointmentRepository)
    : IRequestHandler<GetMonthlyRevenueReportQuery, Result<MonthlyRevenueReportDto>>
{
    private static readonly CultureInfo PtBr = new("pt-BR");

    public async Task<Result<MonthlyRevenueReportDto>> Handle(GetMonthlyRevenueReportQuery request, CancellationToken cancellationToken)
    {
        var now = DateTime.UtcNow;
        var year = request.Year ?? now.Year;
        var month = request.Month ?? now.Month;

        if (month < 1 || month > 12)
            return Result<MonthlyRevenueReportDto>.Failure("Month must be between 1 and 12.");

        var monthStart = new DateTime(year, month, 1, 0, 0, 0, DateTimeKind.Utc);
        var monthEnd = monthStart.AddMonths(1).AddTicks(-1); // último instante do mês

        // Busca todos os atendimentos FINALIZADOS no período (somente Finished gera receita)
        var appointments = await appointmentRepository.GetFinishedByDateRangeAsync(
            monthStart, monthEnd, cancellationToken);

        var appointmentsList = appointments.ToList();

        var totalAppointments = appointmentsList.Count;
        var totalRevenue = appointmentsList.Sum(a => a.Service.Price);
        var averageTicket = totalAppointments > 0
            ? Math.Round(totalRevenue / totalAppointments, 2)
            : 0;

        // "Serviço Estrela" — agrupa por serviço e encontra o de maior receita total
        TopServiceDto? starService = null;
        if (appointmentsList.Count > 0)
        {
            var topGroup = appointmentsList
                .GroupBy(a => new { a.ServiceId, a.Service.Name })
                .Select(g => new TopServiceDto(
                    ServiceId: g.Key.ServiceId,
                    ServiceName: g.Key.Name,
                    TimesPerformed: g.Count(),
                    TotalRevenue: g.Sum(a => a.Service.Price)
                ))
                .OrderByDescending(s => s.TotalRevenue)
                .First();

            starService = topGroup;
        }

        var monthName = CapitalizeFirst(
            new DateTime(year, month, 1).ToString("MMMM 'de' yyyy", PtBr));

        var report = new MonthlyRevenueReportDto(
            Year: year,
            Month: month,
            MonthName: monthName,
            TotalRevenue: Math.Round(totalRevenue, 2),
            TotalAppointments: totalAppointments,
            AverageTicket: averageTicket,
            StarService: starService
        );

        return Result<MonthlyRevenueReportDto>.Success(report);
    }

    private static string CapitalizeFirst(string text)
        => string.IsNullOrEmpty(text) ? text : char.ToUpper(text[0], PtBr) + text[1..];
}