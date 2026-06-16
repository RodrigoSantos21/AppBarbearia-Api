namespace AppBarbearia.Domain.Entities;

public class Service : BaseEntity
{
    public string Name            { get; private set; } = string.Empty;
    public string Description     { get; private set; } = string.Empty;
    public decimal Price          { get; private set; }
    public int DurationMinutes    { get; private set; }
    public bool IsActive          { get; private set; }

    // Navigation
    public ICollection<Appointment> Appointments { get; private set; } = new List<Appointment>();

    private Service() { }

    public static Service Create(string name, string description, decimal price, int durationMinutes)
        => new()
        {
            Name            = name,
            Description     = description,
            Price           = price,
            DurationMinutes = durationMinutes,
            IsActive        = true
        };

    public void Update(string name, string description, decimal price, int durationMinutes)
    {
        Name            = name;
        Description     = description;
        Price           = price;
        DurationMinutes = durationMinutes;
        SetUpdatedAt();
    }

    public void Deactivate() { IsActive = false; SetUpdatedAt(); }
    public void Activate()   { IsActive = true;  SetUpdatedAt(); }
}
