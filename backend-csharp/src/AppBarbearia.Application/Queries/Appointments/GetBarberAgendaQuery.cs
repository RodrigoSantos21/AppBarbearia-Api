using MediatR;
using AppBarbearia.Application.Common;

namespace AppBarbearia.Application.Queries.Appointments;

public record GetBarberAgendaQuery(Guid BarberId, DateTime Date) : IRequest<Result<IEnumerable<AppointmentDto>>>;
