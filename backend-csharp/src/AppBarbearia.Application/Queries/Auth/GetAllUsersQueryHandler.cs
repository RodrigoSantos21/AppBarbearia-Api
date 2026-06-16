using MediatR;
using AppBarbearia.Application.Common;
using AppBarbearia.Domain.Enums;
using AppBarbearia.Domain.Interfaces;

namespace AppBarbearia.Application.Queries.Auth;

public sealed class GetAllUsersQueryHandler(IUserRepository userRepository)
    : IRequestHandler<GetAllUsersQuery, Result<IEnumerable<UserDto>>>
{
    public async Task<Result<IEnumerable<UserDto>>> Handle(GetAllUsersQuery request, CancellationToken cancellationToken)
    {
        var all = await userRepository.GetAllAsync(cancellationToken);

        var query = all.AsEnumerable();

        // Filtra por role se especificado (ex: "Barber", "Client", "Admin")
        if (!string.IsNullOrWhiteSpace(request.Role))
        {
            if (!Enum.TryParse<UserRole>(request.Role, ignoreCase: true, out var role))
                return Result<IEnumerable<UserDto>>.Failure($"Invalid role: '{request.Role}'.");

            query = query.Where(u => u.Role == role);
        }

        var users = query
            .OrderByDescending(u => u.CreatedAt)
            .Select(u => new UserDto(u.Id, u.FullName, u.Email, u.Phone, u.Role.ToString(), u.IsActive, u.CreatedAt));

        return Result<IEnumerable<UserDto>>.Success(users);
    }
}