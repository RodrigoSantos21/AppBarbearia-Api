using MediatR;
using AppBarbearia.Application.Common;

namespace AppBarbearia.Application.Queries.Auth;

public record GetAllBarbersQuery : IRequest<Result<IEnumerable<UserDto>>>;
