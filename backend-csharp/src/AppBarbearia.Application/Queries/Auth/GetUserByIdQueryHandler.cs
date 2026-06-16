using MediatR;
using AppBarbearia.Application.Common;
using AppBarbearia.Domain.Interfaces;

namespace AppBarbearia.Application.Queries.Auth;

public sealed class GetUserByIdQueryHandler(IUserRepository userRepository)
    : IRequestHandler<GetUserByIdQuery, Result<UserDto>>
{
    public async Task<Result<UserDto>> Handle(GetUserByIdQuery request, CancellationToken cancellationToken)
    {
        var user = await userRepository.GetByIdAsync(request.UserId, cancellationToken);
        if (user is null)
            return Result<UserDto>.Failure("User not found.");

        return Result<UserDto>.Success(
            new UserDto(user.Id, user.FullName, user.Email, user.Phone, user.Role.ToString(), user.IsActive, user.CreatedAt));
    }
}
