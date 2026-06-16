namespace AppBarbearia.Application.Common;

/// <summary>
/// Serviço com maior receita no período — "Serviço Estrela".
/// </summary>
public record TopServiceDto(
    Guid ServiceId,
    string ServiceName,
    int TimesPerformed,
    decimal TotalRevenue
);

/// <summary>
/// Relatório de faturamento e ticket médio do mês — visão Admin.
/// </summary>
public record MonthlyRevenueReportDto(
    int Year,
    int Month,
    string MonthName,        // "Abril de 2026"
    decimal TotalRevenue,      // soma do preço de todos os atendimentos Finished no mês
    int TotalAppointments, // total de atendimentos Finished no mês
    decimal AverageTicket,     // TotalRevenue / TotalAppointments
    TopServiceDto? StarService  // serviço que mais gerou receita ("Serviço Estrela")
);