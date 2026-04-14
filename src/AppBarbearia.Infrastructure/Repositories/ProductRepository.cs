using Microsoft.EntityFrameworkCore;
using AppBarbearia.Domain.Entities;
using AppBarbearia.Domain.Interfaces;
using AppBarbearia.Infrastructure.Data;

namespace AppBarbearia.Infrastructure.Repositories;

public class ProductRepository(AppDbContext context)
    : Repository<Product>(context), IProductRepository
{
    public async Task<IEnumerable<Product>> GetActiveProductsAsync(CancellationToken cancellationToken = default)
        => await DbSet
            .AsNoTracking()
            .Where(p => p.IsActive)
            .ToListAsync(cancellationToken);

    public async Task<bool> ExistsByNameAsync(string name, Guid? excludeId = null, CancellationToken cancellationToken = default)
        => await DbSet.AnyAsync(
            p => p.Name == name && (excludeId == null || p.Id != excludeId.Value),
            cancellationToken);
}
