/* ================================
   1) CREATE DATABASE
   ================================ */
IF DB_ID(N'jwt_nimbus_demo') IS NULL
BEGIN
    CREATE DATABASE jwt_nimbus_demo;
END
GO

USE jwt_nimbus_demo;
GO

/* ================================
   2) DROP TABLE (optional)
   ================================ */
IF OBJECT_ID(N'dbo.users', N'U') IS NOT NULL
BEGIN
    DROP TABLE dbo.users;
END
GO

/* ================================
   3) CREATE TABLE dbo.users
   ================================ */
CREATE TABLE dbo.users (
    id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    full_name NVARCHAR(100) NOT NULL,
    email NVARCHAR(100) NOT NULL,
    images NVARCHAR(255) NOT NULL CONSTRAINT DF_users_images DEFAULT(N'/images/u1.jpg'),
    password NVARCHAR(255) NOT NULL,
    created_at DATETIME2(0) NOT NULL CONSTRAINT DF_users_created_at DEFAULT(SYSDATETIME()),
    updated_at DATETIME2(0) NULL
);
GO

CREATE UNIQUE INDEX UX_users_email ON dbo.users(email);
GO

/* ================================
   4) INSERT DATA (login được ngay)
   Password demo: 123456
   ================================ */
SET IDENTITY_INSERT dbo.users ON;
INSERT INTO dbo.users (id, full_name, email, images, password, created_at, updated_at)
VALUES
(1, N'Admin System', N'admin@gmail.com', N'/images/u1.jpg', N'{noop}123456', SYSDATETIME(), SYSDATETIME()),
(2, N'Nguyễn Hữu Trung', N'trung@hcmute.edu.vn', N'/images/u1.jpg', N'{noop}123456', SYSDATETIME(), SYSDATETIME()),
(3, N'Student 01', N'sv01@hcmute.edu.vn', N'/images/u1.jpg', N'{noop}123456', SYSDATETIME(), SYSDATETIME());
SET IDENTITY_INSERT dbo.users OFF;
GO

SELECT id, full_name, email, images, created_at, updated_at
FROM dbo.users;
GO