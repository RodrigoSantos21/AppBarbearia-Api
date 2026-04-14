using AppBarbearia.Domain.Entities;

namespace AppBarbearia.Domain.Interfaces;

public interface ITokenService
{
    string GenerateToken(User user);
}
