using MediatR;
using AppBarbearia.Application.Common;
using AppBarbearia.Domain.Interfaces;

namespace AppBarbearia.Application.Commands.Auth;

public sealed class SetUserActiveStatusCommandHandler(IUserRepository userRepository)
    : IRequestHandler<SetUserActiveStatusCommand, Result>
{
    public async Task<Result> Handle(SetUserActiveStatusCommand request, CancellationToken cancellationToken)
    {
        var user = await userRepository.GetByIdAsync(request.UserId, cancellationToken);
        if (user is null)
            return Result.Failure("User not found.");

        if (request.IsActive)
            user.Activate();
        else
            user.Deactivate();

        userRepository.Update(user);
        await userRepository.SaveChangesAsync(cancellationToken);

        return Result.Success();
    }
}