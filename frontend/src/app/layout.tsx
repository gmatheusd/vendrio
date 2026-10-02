import type { Metadata } from "next";
import Link from "next/link";
import "./globals.css";

export const metadata: Metadata = {
  title: "Vendrio",
  description: "Vendrio - sitema de caixa e controle de estoque",
};

const menu = [
  { href: "/", label: "Início" },
  { href: "/caixa", label: "Caixa (PDV)" },
  { href: "/produtos", label: "Produtos" },
  { href: "/entradas", label: "Entradas" },
  { href: "/relatorios", label: "Relatórios" },
];


export default function RootLayout({ children }: LayoutProps<"/">) {
  return (
    <html lang="pt-BR" className="h-full antialiased">
      <body className="min-h-full flex">{children}
        <aside className="w-56 shrink-0 border-r border-black/10 dark:border-white/10 p-4">
          <nav className="flex flex-col gap-1">
              {menu.map((item) => (
                <Link
                  key={item.href}
                  href={item.href}
                  className="rounded px-3 py-2 hover:bg-black/5 dark:hover:bg-white/10"
                >
                  {item.label}
                </Link>
              ))}
            </nav>
        </aside>
      </body>
    </html>
  );
}
