using MediatR;
using AppBarbearia.Application.Common;
using AppBarbearia.Domain.Interfaces;

namespace AppBarbearia.Application.Commands.Appointments;

public sealed class RescheduleAppointmentCommandHandler(
    IAppointmentRepository appointmentRepository,
    IServiceRepository     serviceRepository)
    : IRequestHandler<RescheduleAppointmentCommand, Result>
{
    public async Task<Result> Handle(RescheduleAppointmentCommand request, CancellationToken cancellationToken)
    {
        var appointment = await appointmentRepository.GetByIdAsync(request.AppointmentId, cancellationToken);
        if (appointment is null)
            return Result.Failure("Appointment not found.");

        if (appointment.ClientId != request.RequestedByUserId &&
            appointment.BarberId != request.RequestedByUserId)
            return Result.Failure("You do not have permission to reschedule this appointment.");

        if (request.NewScheduledAt <= DateTime.UtcNow)
            return Result.Failure("New scheduled time must be in the future.");

        var service = await serviceRepository.GetByIdAsync(appointment.ServiceId, cancellationToken);
        if (service is null)
            return Result.Failure("Associated service not found.");

        var hasConflict = await appointmentRepository.HasConflictAsync(
            appointment.BarberId, request.NewScheduledAt, service.DurationMinutes,
            excludeId: appointment.Id, cancellationToken: cancellationToken);
        if (hasConflict)
            return Result.Failure("The barber already has an appointment at the new time.");

        try
        {
            appointment.Reschedule(request.NewScheduledAt);
        }
        catch (InvalidOperationException ex)
        {
            return Result.Failure(ex.Message);
        }

        appointmentRepository.Update(appointment);
        await appointmentRepository.SaveChangesAsync(cancellationToken);

        return Result.Success();
    }
}
