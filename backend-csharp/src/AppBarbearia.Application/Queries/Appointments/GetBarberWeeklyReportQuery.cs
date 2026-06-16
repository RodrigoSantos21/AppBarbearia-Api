using MediatR;
using AppBarbearia.Application.Common;

namespace AppBarbearia.Application.Queries.Appointments;

/// <summary>
/// Relatório 1 — Ocupação e Produtividade (visão Barbeiro).
/// Mostra total de atendimentos finalizados vs cancelados na semana atual,
/// com breakdown por dia.
/// </summary>
/// <param name="BarberId">ID do barbeiro logado (extraído do JWT).</param>
/// <param name="WeekStart">
/// Opcional. Data de início da semana (segunda-feira).
/// Se não informado, usa a semana atual.
/// </param>
public record GetBarberWeeklyReportQuery(
    Guid BarberId,
    DateOnly? WeekStart = null
) : IRequest<Result<BarberWeeklyReportDto>>;