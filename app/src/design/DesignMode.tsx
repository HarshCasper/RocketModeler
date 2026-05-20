export function DesignMode() {
  return (
    <div className="grid grid-cols-1 lg:grid-cols-[1fr_360px] gap-4 p-4 h-full">
      <section className="rounded-lg border border-nasa/15 bg-white shadow-sm grid place-items-center min-h-[400px]">
        <p className="text-ink/40 text-sm">Rocket viewer goes here</p>
      </section>
      <aside className="rounded-lg border border-nasa/15 bg-white shadow-sm p-4">
        <p className="text-ink/40 text-sm">Design panel goes here</p>
      </aside>
    </div>
  );
}
