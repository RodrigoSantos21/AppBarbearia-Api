using MediatR;
using AppBarbearia.Application.Common;
using AppBarbearia.Domain.Enums;
using AppBarbearia.Domain.Interfaces;

namespace AppBarbearia.Application.Queries.Auth;

public sealed class GetAllBarbersQueryHandler(IUserRepository userRepository)
    : IRequestHandler<GetAllBarbersQuery, Result<IEnumerable<UserDto>>>
{
    public async Task<Result<IEnumerable<UserDto>>> Handle(GetAllBarbersQuery request, CancellationToken cancellationToken)
    {
        var all = await userRepository.GetAllAsync(cancellationToken);
        var barbers = all
            .Where(u => u.Role == UserRole.Barber && u.IsActive)
            .Select(u => new UserDto(u.Id, u.FullName, u.Email, u.Phone, u.Role.ToString(), u.IsActive, u.CreatedAt));

        return Result<IEnumerable<UserDto>>.Success(barbers);
    }
}
