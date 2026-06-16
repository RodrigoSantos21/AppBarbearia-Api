using MediatR;
using AppBarbearia.Application.Common;

namespace AppBarbearia.Application.Commands.Appointments;

public record RescheduleAppointmentCommand(
    Guid     AppointmentId,
    Guid     RequestedByUserId,
    DateTime NewScheduledAt
) : IRequest<Result>;
