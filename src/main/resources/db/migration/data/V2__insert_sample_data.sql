insert into products (id, code, name, description, image_url, disabled, created_by, created_at) values
(1, 'intellij', 'IntelliJ IDEA', 'JetBrains IDE for Java', 'https://resources.jetbrains.com/storage/products/company/brand/logos/IntelliJ_IDEA.png', false, 'admin', '2024-03-01 00:00:00'),
(2, 'goland','GoLand', 'JetBrains IDE for Go', 'https://resources.jetbrains.com/storage/products/company/brand/logos/GoLand.png',false, 'admin','2024-03-01 00:00:00'),
(3, 'webstorm','WebStorm', 'JetBrains IDE for Web Development','https://resources.jetbrains.com/storage/products/company/brand/logos/WebStorm.png', false, 'admin','2024-03-01 00:00:00'),
(4, 'pycharm','PyCharm', 'JetBrains IDE for Python', 'https://resources.jetbrains.com/storage/products/company/brand/logos/PyCharm.png',false, 'admin','2024-03-01 00:00:00'),
(5, 'rider','Rider', 'JetBrains IDE for .NET', 'https://resources.jetbrains.com/storage/products/company/brand/logos/Rider.png',false, 'admin','2024-03-01 00:00:00')
;

insert into releases (id, product_id, code, description, status, created_by, created_at) values
(1, 1, 'IJ-2023.3.8', 'IntelliJ IDEA 2023.3.8', 'RELEASED', 'admin','2023-03-25'),
(2, 1, 'IJ-2024.2.3', 'IntelliJ IDEA 2024.2.4', 'RELEASED', 'admin','2024-02-25'),
(3, 2, 'GL-2024.2.3', 'GoLand 2024.2.4', 'RELEASED', 'admin','2024-02-15'),
(4, 3, 'WS-2024.2.3', 'WebStorm 2024.2.4', 'RELEASED', 'admin','2024-02-20'),
(5, 4, 'PC-2024.2.3', 'PyCharm 2024.2.4', 'RELEASED', 'admin','2024-02-20'),
(6, 5, 'RI-2024.2.6', 'Rider 2024.2.6', 'RELEASED', 'admin','2024-02-16')
;

insert into features (id, product_id, release_id, code, title, description, status, created_by, assigned_to, created_at) values
(1, 1, 1, 'IJ-10001', 'Redesign Structure Tool Window', 'Redesign Structure Tool Window to show logical structure', 'NEW', 'siva', 'john', '2024-02-24'),
(2, 1, 1, 'IJ-10002', 'SDJ Repository Method AutoCompletion', 'Spring Data JPA Repository Method AutoCompletion as you type', 'NEW', 'john', 'siva', '2024-03-14'),
(3, 2, null, 'GL-10001', 'Make Go to Type and Go to Symbol dumb aware', 'Make Go to Type and Go to Symbol dumb aware', 'IN_DEVELOPMENT', 'paul', 'remo', '2024-01-14')
;
