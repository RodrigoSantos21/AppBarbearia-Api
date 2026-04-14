using MediatR;
using AppBarbearia.Application.Common;

namespace AppBarbearia.Application.Queries.Appointments;

public record GetMyAppointmentsQuery(Guid ClientId) : IRequest<Result<IEnumerable<AppointmentDto>>>;
