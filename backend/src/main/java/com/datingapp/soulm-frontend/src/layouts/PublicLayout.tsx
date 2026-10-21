import { Outlet } from 'react-router-dom';
import { PublicFooter } from '@/components/common/PublicFooter';
import { PublicHeader } from '@/components/common/PublicHeader';

export function PublicLayout() {
  return (
    <>
      <a href="#main" className="skip-link">
        Skip to content
      </a>
      <PublicHeader />
      <main id="main" tabIndex={-1}>
        <Outlet />
      </main>
      <PublicFooter />
    </>
  );
}
