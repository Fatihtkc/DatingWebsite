import React from 'react';

export const Card = ({ children, className }) => {
  return (
    <div className={`bg-white p-4 shadow-lg rounded-lg ${className}`}>
      {children}
    </div>
  );
};

export const CardContent = ({ children }) => {
  return <div className="flex flex-col items-center">{children}</div>;
};