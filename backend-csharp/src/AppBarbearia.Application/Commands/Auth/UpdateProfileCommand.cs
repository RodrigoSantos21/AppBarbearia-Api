using MediatR;
using AppBarbearia.Application.Common;

namespace AppBarbearia.Application.Commands.Auth;

public record UpdateProfileCommand(
    Guid   UserId,
    string FullName,
    string Phone
) : IRequest<Result>;
