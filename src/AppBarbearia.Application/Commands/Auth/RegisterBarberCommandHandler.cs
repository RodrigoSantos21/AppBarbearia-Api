using MediatR;
using AppBarbearia.Application.Common;
using AppBarbearia.Domain.Entities;
using AppBarbearia.Domain.Enums;
using AppBarbearia.Domain.Interfaces;

namespace AppBarbearia.Application.Commands.Auth;

public sealed class RegisterBarberCommandHandler(
    IUserRepository userRepository,
    IPasswordHasher passwordHasher)
    : IRequestHandler<RegisterBarberCommand, Result<Guid>>
{
    public async Task<Result<Guid>> Handle(RegisterBarberCommand request, CancellationToken cancellationToken)
    {
        var exists = await userRepository.ExistsByEmailAsync(request.Email, cancellationToken: cancellationToken);
        if (exists)
            return Result<Guid>.Failure("E-mail already in use.");

        var hash = passwordHasher.Hash(request.Password);
        var user = User.Create(request.FullName, request.Email, request.Phone, hash, UserRole.Barber);

        await userRepository.AddAsync(user, cancellationToken);
        await userRepository.SaveChangesAsync(cancellationToken);

        return Result<Guid>.Success(user.Id);
    }
}
