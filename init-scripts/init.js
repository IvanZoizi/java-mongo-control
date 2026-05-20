db = db.getSiblingDB('mydb');

db.createCollection('products');
db.createCollection('orders');
db.createCollection('users');

db.products.createIndex({ name: "text" });
db.products.createIndex({ category: 1 });
db.products.createIndex({ price: -1 });
db.products.createIndex({ sku: 1 }, { unique: true });

db.orders.createIndex({ userId: 1 });
db.orders.createIndex({ orderDate: -1 });
db.orders.createIndex({ status: 1 });
db.orders.createIndex({ totalAmount: -1 });

db.users.createIndex({ email: 1 }, { unique: true });
db.users.createIndex({ username: 1 });
db.users.createIndex({ role: 1 });

db.products.insertMany([
  { sku: "PRD-001", name: "Ноутбук Lenovo", category: "Электроника", price: 45000, stock: 10, description: "Мощный ноутбук для работы" },
  { sku: "PRD-002", name: "Мышь Logitech", category: "Электроника", price: 1500, stock: 50, description: "Беспроводная мышь" },
  { sku: "PRD-003", name: "Книга Java", category: "Книги", price: 800, stock: 30, description: "Учебник по Java" },
  { sku: "PRD-004", name: "Смартфон Samsung", category: "Электроника", price: 35000, stock: 5, description: "Флагманский смартфон" },
  { sku: "PRD-005", name: "Наушники Sony", category: "Аудио", price: 8000, stock: 20, description: "Беспроводные наушники" }
]);

db.orders.insertMany([
  { orderId: "ORD-001", userId: 1, productSku: "PRD-001", quantity: 1, totalAmount: 45000, status: "Доставлен", orderDate: new Date("2024-01-15") },
  { orderId: "ORD-002", userId: 2, productSku: "PRD-002", quantity: 2, totalAmount: 3000, status: "В обработке", orderDate: new Date("2024-01-20") },
  { orderId: "ORD-003", userId: 1, productSku: "PRD-004", quantity: 1, totalAmount: 35000, status: "Доставлен", orderDate: new Date("2024-02-01") },
  { orderId: "ORD-004", userId: 3, productSku: "PRD-003", quantity: 3, totalAmount: 2400, status: "Отменен", orderDate: new Date("2024-02-10") },
  { orderId: "ORD-005", userId: 2, productSku: "PRD-005", quantity: 1, totalAmount: 8000, status: "В пути", orderDate: new Date("2024-02-15") }
]);

// Создаем пользователей с разными ролями
db.users.insertMany([
  {
    username: "admin",
    email: "admin@example.com",
    password: "admin123",
    role: "ADMIN",
    permissions: ["CREATE", "READ", "UPDATE", "DELETE"]
  },
  {
    username: "editor",
    email: "editor@example.com",
    password: "editor123",
    role: "EDITOR",
    permissions: ["READ", "UPDATE"]
  },
  {
    username: "viewer",
    email: "viewer@example.com",
    password: "viewer123",
    role: "VIEWER",
    permissions: ["READ"]
  }
]);

db.createUser({
  user: "admin",
  pwd: "admin123",
  roles: [
    { role: "readWrite", db: "mydb" },
    { role: "dbAdmin", db: "mydb" }
  ]
});

db.createUser({
  user: "editor",
  pwd: "editor123",
  roles: [{ role: "readWrite", db: "mydb" }]
});

db.createUser({
  user: "viewer",
  pwd: "viewer123",
  roles: [{ role: "read", db: "mydb" }]
});


print("Database initialized successfully!");