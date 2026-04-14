namespace AppBarbearia.Application.Common;

public record AppointmentDto(
    Guid     Id,
    Guid     ClientId,
    string   ClientName,
    Guid     BarberId,
    string   BarberName,
    Guid     ServiceId,
    string   ServiceName,
    decimal  ServicePrice,
    int      ServiceDurationMinutes,
    DateTime ScheduledAt,
    DateTime ScheduledEnd,
    string   Status,
    string?  CancellationReason,
    DateTime CreatedAt,
    DateTime? UpdatedAt
);
