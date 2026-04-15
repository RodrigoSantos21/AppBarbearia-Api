using MediatR;
using AppBarbearia.Application.Common;

namespace AppBarbearia.Application.Queries.Appointments;

public record GetAppointmentByIdQuery(Guid Id) : IRequest<Result<AppointmentDto>>;
