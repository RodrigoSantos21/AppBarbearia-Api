using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using AppBarbearia.Domain.Entities;

namespace AppBarbearia.Infrastructure.Data;

public class AppointmentConfiguration : IEntityTypeConfiguration<Appointment>
{
    public void Configure(EntityTypeBuilder<Appointment> builder)
    {
        builder.HasKey(a => a.Id);
        builder.Property(a => a.Id).ValueGeneratedNever();

        builder.Property(a => a.ScheduledAt).IsRequired();
        builder.Property(a => a.Status).IsRequired();
        builder.Property(a => a.CancellationReason).HasMaxLength(500);

        // Client → Appointment (restrict delete: client cannot be deleted while appointments exist)
        builder.HasOne(a => a.Client)
            .WithMany()
            .HasForeignKey(a => a.ClientId)
            .OnDelete(DeleteBehavior.Restrict);

        // Barber → Appointment (restrict delete)
        builder.HasOne(a => a.Barber)
            .WithMany()
            .HasForeignKey(a => a.BarberId)
            .OnDelete(DeleteBehavior.Restrict);

        // Service → Appointment (restrict delete)
        builder.HasOne(a => a.Service)
            .WithMany(s => s.Appointments)
            .HasForeignKey(a => a.ServiceId)
            .OnDelete(DeleteBehavior.Restrict);

        // Index for fast barber schedule lookup
        builder.HasIndex(a => new { a.BarberId, a.ScheduledAt });
        builder.HasIndex(a => a.ClientId);
        builder.HasIndex(a => a.Status);
    }
}
