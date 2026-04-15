using MediatR;
using AppBarbearia.Application.Common;
using AppBarbearia.Domain.Interfaces;

namespace AppBarbearia.Application.Queries.Appointments;

public sealed class GetAppointmentByIdQueryHandler(IAppointmentRepository repository)
    : IRequestHandler<GetAppointmentByIdQuery, Result<AppointmentDto>>
{
    public async Task<Result<AppointmentDto>> Handle(GetAppointmentByIdQuery request, CancellationToken cancellationToken)
    {
        var a = await repository.GetByIdWithDetailsAsync(request.Id, cancellationToken);
        if (a is null)
            return Result<AppointmentDto>.Failure("Appointment not found.");

        return Result<AppointmentDto>.Success(new AppointmentDto(
            a.Id,
            a.ClientId,  a.Client.FullName,
            a.BarberId,  a.Barber.FullName,
            a.ServiceId, a.Service.Name, a.Service.Price, a.Service.DurationMinutes,
            a.ScheduledAt,
            a.ScheduledAt.AddMinutes(a.Service.DurationMinutes),
            a.Status.ToString(),
            a.CancellationReason,
            a.CreatedAt, a.UpdatedAt));
    }
}
