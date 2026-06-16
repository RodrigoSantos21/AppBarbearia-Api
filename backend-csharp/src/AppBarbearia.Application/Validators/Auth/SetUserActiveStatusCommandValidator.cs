using FluentValidation;
using AppBarbearia.Application.Commands.Auth;

namespace AppBarbearia.Application.Validators.Auth;

public class SetUserActiveStatusCommandValidator : AbstractValidator<SetUserActiveStatusCommand>
{
    public SetUserActiveStatusCommandValidator()
    {
        RuleFor(x => x.UserId)
            .NotEmpty().WithMessage("UserId is required.");
    }
}