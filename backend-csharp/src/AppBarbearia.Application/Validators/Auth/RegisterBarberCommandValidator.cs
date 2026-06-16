using FluentValidation;
using AppBarbearia.Application.Commands.Auth;

namespace AppBarbearia.Application.Validators.Auth;

public class RegisterBarberCommandValidator : AbstractValidator<RegisterBarberCommand>
{
    public RegisterBarberCommandValidator()
    {
        RuleFor(x => x.FullName).NotEmpty().MaximumLength(150);
        RuleFor(x => x.Email).NotEmpty().EmailAddress();
        RuleFor(x => x.Phone).NotEmpty().Matches(@"^\+?[\d\s\-()]{7,20}$");
        RuleFor(x => x.Password).NotEmpty().MinimumLength(8)
            .Matches("[A-Z]").WithMessage("Password must contain an uppercase letter.")
            .Matches("[0-9]").WithMessage("Password must contain a number.");
    }
}
