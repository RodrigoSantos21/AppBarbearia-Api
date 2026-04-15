using FluentValidation;
using AppBarbearia.Application.Commands.Auth;

namespace AppBarbearia.Application.Validators.Auth;

public class UpdateProfileCommandValidator : AbstractValidator<UpdateProfileCommand>
{
    public UpdateProfileCommandValidator()
    {
        RuleFor(x => x.UserId).NotEmpty();
        RuleFor(x => x.FullName).NotEmpty().MaximumLength(150);
        RuleFor(x => x.Phone).NotEmpty().Matches(@"^\+?[\d\s\-()]{7,20}$");
    }
}
