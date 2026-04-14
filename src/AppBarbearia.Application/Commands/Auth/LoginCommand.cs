using MediatR;
using AppBarbearia.Application.Common;

namespace AppBarbearia.Application.Commands.Auth;

public record LoginCommand(
    string Email,
    string Password
) : IRequest<Result<AuthResponseDto>>;
