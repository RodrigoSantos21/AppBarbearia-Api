using AppBarbearia.Domain.Enums;

namespace AppBarbearia.Domain.Entities;

public class User : BaseEntity
{
    public string FullName    { get; private set; } = string.Empty;
    public string Email       { get; private set; } = string.Empty;
    public string Phone       { get; private set; } = string.Empty;
    public string PasswordHash { get; private set; } = string.Empty;
    public UserRole Role      { get; private set; }
    public bool IsActive      { get; private set; }

    private User() { }

    public static User Create(string fullName, string email, string phone, string passwordHash, UserRole role)
        => new()
        {
            FullName     = fullName,
            Email        = email.ToLowerInvariant(),
            Phone        = phone,
            PasswordHash = passwordHash,
            Role         = role,
            IsActive     = true
        };

    public void UpdateProfile(string fullName, string phone)
    {
        FullName = fullName;
        Phone    = phone;
        SetUpdatedAt();
    }

    public void ChangePassword(string newPasswordHash)
    {
        PasswordHash = newPasswordHash;
        SetUpdatedAt();
    }

    public void Deactivate() { IsActive = false; SetUpdatedAt(); }
    public void Activate()   { IsActive = true;  SetUpdatedAt(); }
}
