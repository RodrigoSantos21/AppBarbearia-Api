using MediatR;
using AppBarbearia.Application.Common;

namespace AppBarbearia.Application.Commands.Appointments;

public record CancelAppointmentCommand(
    Guid   AppointmentId,
    Guid   RequestedByUserId,
    string Reason
) : IRequest<Result>;
