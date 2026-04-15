using MediatR;
using AppBarbearia.Application.Common;

namespace AppBarbearia.Application.Queries.Auth;

public record GetUserByIdQuery(Guid UserId) : IRequest<Result<UserDto>>;
