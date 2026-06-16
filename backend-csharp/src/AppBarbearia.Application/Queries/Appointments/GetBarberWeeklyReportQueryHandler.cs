using MediatR;
using AppBarbearia.Application.Common;
using AppBarbearia.Domain.Enums;
using AppBarbearia.Domain.Interfaces;
using System.Globalization;

namespace AppBarbearia.Application.Queries.Appointments;

public sealed class GetBarberWeeklyReportQueryHandler(
    IAppointmentRepository appointmentRepository,
    IUserRepository userRepository)
    : IRequestHandler<GetBarberWeeklyReportQuery, Result<BarberWeeklyReportDto>>
{
    private static readonly CultureInfo PtBr = new("pt-BR");

    public async Task<Result<BarberWeeklyReportDto>> Handle(GetBarberWeeklyReportQuery request, CancellationToken cancellationToken)
    {
        var barber = await userRepository.GetByIdAsync(request.BarberId, cancellationToken);
        if (barber is null)
            return Result<BarberWeeklyReportDto>.Failure("Barber not found.");

        // Calcula o início da semana (segunda-feira) — usa a semana atual se não especificado
        var referenceDate = request.WeekStart ?? DateOnly.FromDateTime(DateTime.UtcNow);
        var weekStart = StartOfWeek(referenceDate);
        var weekEnd = weekStart.AddDays(6); // domingo

        // Busca todos os agendamentos do barbeiro na semana
        // FIX: DateOnly.ToDateTime() gera Kind=Unspecified; Postgres exige Kind=Utc
        var rangeStart = DateTime.SpecifyKind(weekStart.ToDateTime(TimeOnly.MinValue), DateTimeKind.Utc);
        var rangeEnd = DateTime.SpecifyKind(weekEnd.ToDateTime(TimeOnly.MaxValue), DateTimeKind.Utc);

        var appointments = await appointmentRepository.GetByBarberAndDateRangeAsync(
            request.BarberId,
            rangeStart,
            rangeEnd,
            cancellationToken);

        var dailyBreakdown = new List<DailyOccupancyDto>();

        for (var day = weekStart; day <= weekEnd; day = day.AddDays(1))
        {
            var dayAppointments = appointments.Where(a =>
                DateOnly.FromDateTime(a.ScheduledAt) == day);

            dailyBreakdown.Add(new DailyOccupancyDto(
                Date: day,
                DayOfWeek: CapitalizeFirst(day.ToDateTime(TimeOnly.MinValue).ToString("dddd", PtBr)),
                FinishedCount: dayAppointments.Count(a => a.Status == AppointmentStatus.Finished),
                CancelledCount: dayAppointments.Count(a => a.Status == AppointmentStatus.Cancelled),
                ConfirmedCount: dayAppointments.Count(a => a.Status == AppointmentStatus.Confirmed),
                PendingCount: dayAppointments.Count(a => a.Status == AppointmentStatus.Pending)
            ));
        }

        var totalFinished = dailyBreakdown.Sum(d => d.FinishedCount);
        var totalCancelled = dailyBreakdown.Sum(d => d.CancelledCount);
        var totalConfirmed = dailyBreakdown.Sum(d => d.ConfirmedCount);
        var totalPending = dailyBreakdown.Sum(d => d.PendingCount);

        var totalConcluded = totalFinished + totalCancelled;
        var completionRate = totalConcluded > 0
            ? Math.Round((double)totalFinished / totalConcluded * 100, 1)
            : 0;

        // Receita da semana: soma do preço dos serviços nos agendamentos finalizados
        var totalRevenue = appointments
            .Where(a => a.Status == AppointmentStatus.Finished)
            .Sum(a => a.Service.Price);

        var report = new BarberWeeklyReportDto(
            BarberId: barber.Id,
            BarberName: barber.FullName,
            WeekStart: weekStart,
            WeekEnd: weekEnd,
            TotalFinished: totalFinished,
            TotalCancelled: totalCancelled,
            TotalConfirmed: totalConfirmed,
            TotalPending: totalPending,
            CompletionRate: completionRate,
            TotalRevenue: Math.Round(totalRevenue, 2),
            DailyBreakdown: dailyBreakdown
        );

        return Result<BarberWeeklyReportDto>.Success(report);
    }

    /// <summary>Retorna a segunda-feira da semana que contém a data informada.</summary>
    private static DateOnly StartOfWeek(DateOnly date)
    {
        // DayOfWeek: Sunday = 0, Monday = 1, ..., Saturday = 6
        var diff = (7 + (int)date.DayOfWeek - (int)DayOfWeek.Monday) % 7;
        return date.AddDays(-diff);
    }

    private static string CapitalizeFirst(string text)
        => string.IsNullOrEmpty(text) ? text : char.ToUpper(text[0], PtBr) + text[1..];
}