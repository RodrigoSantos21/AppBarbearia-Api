using MediatR;
using AppBarbearia.Application.Common;
using AppBarbearia.Domain.Interfaces;

namespace AppBarbearia.Application.Commands.Appointments;

public sealed class CancelAppointmentCommandHandler(IAppointmentRepository repository)
    : IRequestHandler<CancelAppointmentCommand, Result>
{
    public async Task<Result> Handle(CancelAppointmentCommand request, CancellationToken cancellationToken)
    {
        var appointment = await repository.GetByIdAsync(request.AppointmentId, cancellationToken);
        if (appointment is null)
            return Result.Failure("Appointment not found.");

        if (appointment.ClientId != request.RequestedByUserId &&
            appointment.BarberId != request.RequestedByUserId)
            return Result.Failure("You do not have permission to cancel this appointment.");

        try
        {
            appointment.Cancel(request.Reason);
        }
        catch (InvalidOperationException ex)
        {
            return Result.Failure(ex.Message);
        }

        repository.Update(appointment);
        await repository.SaveChangesAsync(cancellationToken);

        return Result.Success();
    }
}
