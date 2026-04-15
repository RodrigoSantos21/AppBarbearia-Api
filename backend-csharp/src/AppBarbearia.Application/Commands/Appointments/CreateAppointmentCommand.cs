using MediatR;
using AppBarbearia.Application.Common;

namespace AppBarbearia.Application.Commands.Appointments;

public record CreateAppointmentCommand(
    Guid     ClientId,
    Guid     BarberId,
    Guid     ServiceId,
    DateTime ScheduledAt
) : IRequest<Result<Guid>>;
