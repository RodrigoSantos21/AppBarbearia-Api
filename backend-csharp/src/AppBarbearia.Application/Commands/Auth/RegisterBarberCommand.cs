using MediatR;
using AppBarbearia.Application.Common;

namespace AppBarbearia.Application.Commands.Auth;

public record RegisterBarberCommand(
    string FullName,
    string Email,
    string Phone,
    string Password
) : IRequest<Result<Guid>>;
