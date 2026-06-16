using MediatR;
using AppBarbearia.Application.Common;

namespace AppBarbearia.Application.Queries.Appointments;

/// <summary>
/// Relatório 2 — Faturamento e Ticket Médio (visão Admin).
/// Calcula o valor total arrecadado no mês e o "Serviço Estrela"
/// (serviço que mais gerou receita).
/// </summary>
/// <param name="Year">Ano do relatório. Se null, usa o ano atual.</param>
/// <param name="Month">Mês do relatório (1-12). Se null, usa o mês atual.</param>
public record GetMonthlyRevenueReportQuery(
    int? Year = null,
    int? Month = null
) : IRequest<Result<MonthlyRevenueReportDto>>;