using Microsoft.EntityFrameworkCore;
using AppBarbearia.Domain.Entities;
using AppBarbearia.Domain.Interfaces;
using AppBarbearia.Infrastructure.Data;

namespace AppBarbearia.Infrastructure.Repositories;

public class ServiceRepository(AppDbContext context)
    : Repository<Service>(context), IServiceRepository
{
    public async Task<IEnumerable<Service>> GetActiveServicesAsync(CancellationToken cancellationToken = default)
        => await DbSet.AsNoTracking().Where(s => s.IsActive).ToListAsync(cancellationToken);

    public async Task<bool> ExistsByNameAsync(string name, Guid? excludeId = null, CancellationToken cancellationToken = default)
        => await DbSet.AnyAsync(
            s => s.Name == name && (excludeId == null || s.Id != excludeId.Value),
            cancellationToken);
}
