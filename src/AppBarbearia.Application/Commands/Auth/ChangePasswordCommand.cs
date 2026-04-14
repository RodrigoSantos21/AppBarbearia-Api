using MediatR;
using AppBarbearia.Application.Common;

namespace AppBarbearia.Application.Commands.Auth;

public record ChangePasswordCommand(
    Guid   UserId,
    string CurrentPassword,
    string NewPassword,
    string ConfirmNewPassword
) : IRequest<Result>;
