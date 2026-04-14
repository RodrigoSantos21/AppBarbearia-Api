using MediatR;
using AppBarbearia.Application.Common;
using AppBarbearia.Domain.Interfaces;

namespace AppBarbearia.Application.Commands.Appointments;

public sealed class FinishAppointmentCommandHandler(IAppointmentRepository repository)
    : IRequestHandler<FinishAppointmentCommand, Result>
{
    public async Task<Result> Handle(FinishAppointmentCommand request, CancellationToken cancellationToken)
    {
        var appointment = await repository.GetByIdAsync(request.AppointmentId, cancellationToken);
        if (appointment is null)
            return Result.Failure("Appointment not found.");

        if (appointment.BarberId != request.BarberId)
            return Result.Failure("You are not the barber for this appointment.");

        try
        {
            appointment.Finish();
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
