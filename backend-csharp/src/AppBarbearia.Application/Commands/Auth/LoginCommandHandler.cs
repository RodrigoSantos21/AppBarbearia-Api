using MediatR;
using AppBarbearia.Application.Common;
using AppBarbearia.Domain.Interfaces;

namespace AppBarbearia.Application.Commands.Auth;

public sealed class LoginCommandHandler(
    IUserRepository userRepository,
    IPasswordHasher passwordHasher,
    ITokenService   tokenService)
    : IRequestHandler<LoginCommand, Result<AuthResponseDto>>
{
    public async Task<Result<AuthResponseDto>> Handle(LoginCommand request, CancellationToken cancellationToken)
    {
        var user = await userRepository.GetByEmailAsync(request.Email, cancellationToken);
        if (user is null || !user.IsActive)
            return Result<AuthResponseDto>.Failure("Invalid credentials.");

        if (!passwordHasher.Verify(request.Password, user.PasswordHash))
            return Result<AuthResponseDto>.Failure("Invalid credentials.");

        var token = tokenService.GenerateToken(user);

        var response = new AuthResponseDto(
            AccessToken: token,
            TokenType: "Bearer",
            ExpiresIn: 3600,
            User: new UserDto(user.Id, user.FullName, user.Email, user.Phone, user.Role.ToString(), user.IsActive, user.CreatedAt));

        return Result<AuthResponseDto>.Success(response);
    }
}
