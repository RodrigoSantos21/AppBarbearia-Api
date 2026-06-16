using MediatR;
using AppBarbearia.Application.Common;

namespace AppBarbearia.Application.Queries.Auth;

/// <summary>
/// Lista todos os usuários (clientes, barbeiros, admins),
/// incluindo inativos — usado pelo Admin para gestão de contas.
/// Opcionalmente filtra por role.
/// </summary>
public record GetAllUsersQuery(string? Role = null) : IRequest<Result<IEnumerable<UserDto>>>;