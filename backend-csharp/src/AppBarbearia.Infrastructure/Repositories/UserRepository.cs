using Microsoft.EntityFrameworkCore;
using AppBarbearia.Domain.Entities;
using AppBarbearia.Domain.Interfaces;
using AppBarbearia.Infrastructure.Data;

namespace AppBarbearia.Infrastructure.Repositories;

public class UserRepository(AppDbContext context)
    : Repository<User>(context), IUserRepository
{
    public async Task<User?> GetByEmailAsync(string email, CancellationToken cancellationToken = default)
        => await DbSet.FirstOrDefaultAsync(u => u.Email == email.ToLowerInvariant(), cancellationToken);

    public async Task<bool> ExistsByEmailAsync(string email, Guid? excludeId = null, CancellationToken cancellationToken = default)
        => await DbSet.AnyAsync(
            u => u.Email == email.ToLowerInvariant() && (excludeId == null || u.Id != excludeId.Value),
            cancellationToken);
}
