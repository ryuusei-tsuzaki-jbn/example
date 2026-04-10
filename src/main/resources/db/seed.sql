INSERT INTO app_user (username, password, age, role, active) VALUES
('alice', 'alice123', 20, 'USER', true),
('bob', 'bob123', 17, 'USER', false),
('admin', 'secret', 30, 'ADMIN', true)
ON CONFLICT (username) DO NOTHING;
