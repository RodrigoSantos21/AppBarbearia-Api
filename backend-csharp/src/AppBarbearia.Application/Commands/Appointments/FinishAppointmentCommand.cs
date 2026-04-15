using MediatR;
using AppBarbearia.Application.Common;

namespace AppBarbearia.Application.Commands.Appointments;

public record FinishAppointmentCommand(Guid AppointmentId, Guid BarberId) : IRequest<Result>;
