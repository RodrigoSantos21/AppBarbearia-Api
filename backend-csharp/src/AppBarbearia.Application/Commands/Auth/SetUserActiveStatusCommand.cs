using MediatR;
using AppBarbearia.Application.Common;

namespace AppBarbearia.Application.Commands.Auth;

/// <summary>
/// Ativa ou desativa a conta de um usuário (barbeiro, cliente ou admin).
/// IsActive = false impede login (verificado no LoginCommandHandler).
/// </summary>
public record SetUserActiveStatusCommand(
    Guid UserId,
    bool IsActive
) : IRequest<Result>;