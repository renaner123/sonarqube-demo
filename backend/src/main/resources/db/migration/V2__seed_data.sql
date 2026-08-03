-- Passwords are bcrypt hash of "senha123"
INSERT INTO users (name, email, password_hash) VALUES
    ('Alice Souza',  'alice@demo.com', '$2b$10$LX7hDiB8A4.A2ifzzwgSHOi88SeShMEgjAMhCouOg9j9q0t8qoc/W'),
    ('Bruno Lima',   'bruno@demo.com', '$2b$10$C3hV.Vy3bVHda.7yj/m/G.M5YHPKaOwZFMePZH8VndoGORDRmCY8q');

-- Categories: 3 per user
INSERT INTO categories (name, color, user_id) VALUES
    ('Trabalho', '#6366f1', 1),
    ('Pessoal',  '#10b981', 1),
    ('Estudos',  '#f59e0b', 1),
    ('Trabalho', '#6366f1', 2),
    ('Pessoal',  '#10b981', 2),
    ('Estudos',  '#f59e0b', 2);

-- Tasks — mix of status, priority and dates (some due_date in the past to simulate overdue)
INSERT INTO tasks (title, description, status, priority, due_date, category_id, user_id) VALUES
    ('Finalizar relatório mensal',  'Consolidar dados de abril e enviar para o gerente', 'IN_PROGRESS', 'HIGH',   '2026-05-31', 1, 1),
    ('Revisar pull requests',       'Revisar PRs pendentes do time',                      'TODO',        'MEDIUM', '2026-06-20', 1, 1),
    ('Academia',                     'Treino de musculação — upper/lower split',           'DONE',        'LOW',    '2026-06-10', 2, 1),
    ('Ler Clean Code',               'Capítulos 5 a 8',                                   'IN_PROGRESS', 'MEDIUM', '2026-06-01', 3, 1),
    ('Apresentação para o cliente',  'Preparar slides e demo do sistema',                  'TODO',        'HIGH',   '2026-05-20', 4, 2),
    ('Pagar contas',                 'IPTU, internet e cartão de crédito',                 'DONE',        'HIGH',   '2026-06-05', 5, 2),
    ('Curso de Spring Boot',         'Módulos 3 e 4 da Udemy',                             'IN_PROGRESS', 'MEDIUM', '2026-06-30', 6, 2),
    ('Configurar CI/CD',             'Pipeline GitLab para build e deploy automático',     'TODO',        'HIGH',   '2026-05-15', 4, 2);

-- Comments distributed across tasks
INSERT INTO task_comments (task_id, user_id, content) VALUES
    (1, 1, 'Já tenho os dados de janeiro a março. Falta compilar abril.'),
    (1, 2, 'Posso ajudar com a parte de métricas de vendas se precisar.'),
    (4, 1, 'Terminei o capítulo 5. O trecho sobre funções foi esclarecedor.'),
    (7, 2, 'Módulo 3 concluído. Módulo 4 começa na próxima semana.');
