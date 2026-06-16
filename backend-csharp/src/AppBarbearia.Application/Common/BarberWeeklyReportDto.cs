namespace AppBarbearia.Application.Common;

/// <summary>
/// Resumo de um dia da semana: total de atendimentos finalizados vs cancelados.
/// </summary>
public record DailyOccupancyDto(
    DateOnly Date,
    string DayOfWeek,      // "Segunda-feira", "Terça-feira", etc (em português)
    int FinishedCount,
    int CancelledCount,
    int ConfirmedCount, // ainda não finalizados, mas confirmados
    int PendingCount
);

/// <summary>
/// Relatório semanal de ocupação e produtividade — visão do barbeiro.
/// </summary>
public record BarberWeeklyReportDto(
    Guid BarberId,
    string BarberName,
    DateOnly WeekStart,
    DateOnly WeekEnd,
    int TotalFinished,
    int TotalCancelled,
    int TotalConfirmed,
    int TotalPending,
    double CompletionRate,   // TotalFinished / (TotalFinished + TotalCancelled), em %
    decimal TotalRevenue,     // soma do preço dos serviços finalizados na semana
    IEnumerable<DailyOccupancyDto> DailyBreakdown
);