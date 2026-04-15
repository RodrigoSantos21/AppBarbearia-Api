using AppBarbearia.Domain.Interfaces;

namespace AppBarbearia.Infrastructure.Services;

/// <summary>
/// BCrypt-based password hasher using ASP.NET Core's built-in
/// PasswordHasher as a fallback-safe alternative without extra packages.
/// Uses PBKDF2 via Rfc2898DeriveBytes.
/// </summary>
public sealed class PasswordHasher : IPasswordHasher
{
    private const int Iterations = 350_000;
    private const int SaltSize   = 16;
    private const int HashSize    = 32;

    public string Hash(string password)
    {
        var salt = System.Security.Cryptography.RandomNumberGenerator.GetBytes(SaltSize);
        var hash = Rfc2898DeriveBytes(password, salt);
        return $"{Convert.ToBase64String(salt)}.{Convert.ToBase64String(hash)}";
    }

    public bool Verify(string password, string storedHash)
    {
        var parts = storedHash.Split('.');
        if (parts.Length != 2) return false;

        var salt = Convert.FromBase64String(parts[0]);
        var expectedHash = Convert.FromBase64String(parts[1]);
        var actualHash = Rfc2898DeriveBytes(password, salt);

        return CryptographicEquals(expectedHash, actualHash);
    }

    private static byte[] Rfc2898DeriveBytes(string password, byte[] salt)
    {
        using var pbkdf2 = new System.Security.Cryptography.Rfc2898DeriveBytes(
            password, salt, Iterations, System.Security.Cryptography.HashAlgorithmName.SHA256);
        return pbkdf2.GetBytes(HashSize);
    }

    /// <summary>Constant-time comparison to prevent timing attacks.</summary>
    private static bool CryptographicEquals(byte[] a, byte[] b)
    {
        if (a.Length != b.Length) return false;
        var diff = 0;
        for (var i = 0; i < a.Length; i++) diff |= a[i] ^ b[i];
        return diff == 0;
    }
}
