using FluentValidation;
using AppBarbearia.Application.Commands.Appointments;

namespace AppBarbearia.Application.Validators.Appointments;

public class RescheduleAppointmentCommandValidator : AbstractValidator<RescheduleAppointmentCommand>
{
    public RescheduleAppointmentCommandValidator()
    {
        RuleFor(x => x.AppointmentId).NotEmpty();
        RuleFor(x => x.RequestedByUserId).NotEmpty();
        RuleFor(x => x.NewScheduledAt)
            .GreaterThan(DateTime.UtcNow).WithMessage("New time must be in the future.");
    }
}
