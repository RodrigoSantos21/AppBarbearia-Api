using MediatR;
using AppBarbearia.Application.Common;
using AppBarbearia.Domain.Interfaces;

namespace AppBarbearia.Application.Queries.Appointments;

public sealed class GetMyAppointmentsQueryHandler(IAppointmentRepository repository)
    : IRequestHandler<GetMyAppointmentsQuery, Result<IEnumerable<AppointmentDto>>>
{
    public async Task<Result<IEnumerable<AppointmentDto>>> Handle(GetMyAppointmentsQuery request, CancellationToken cancellationToken)
    {
        var appointments = await repository.GetByClientAsync(request.ClientId, cancellationToken);

        var dtos = appointments.Select(a => new AppointmentDto(
            a.Id,
            a.ClientId,  a.Client.FullName,
            a.BarberId,  a.Barber.FullName,
            a.ServiceId, a.Service.Name, a.Service.Price, a.Service.DurationMinutes,
            a.ScheduledAt,
            a.ScheduledAt.AddMinutes(a.Service.DurationMinutes),
            a.Status.ToString(),
            a.CancellationReason,
            a.CreatedAt, a.UpdatedAt));

        return Result<IEnumerable<AppointmentDto>>.Success(dtos);
    }
}
