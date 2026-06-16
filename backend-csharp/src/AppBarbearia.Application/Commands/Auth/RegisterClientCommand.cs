using MediatR;
using AppBarbearia.Application.Common;

namespace AppBarbearia.Application.Commands.Auth;

public record RegisterClientCommand(
    string FullName,
    string Email,
    string Phone,
    string Password,
    string ConfirmPassword
) : IRequest<Result<Guid>>;
