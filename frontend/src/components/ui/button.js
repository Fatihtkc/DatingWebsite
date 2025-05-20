// src/components/ui/button.js

import React from 'react';

export const Button = ({ children, onClick, className, variant = "primary" }) => {
  const baseStyles = "px-4 py-2 rounded-lg font-semibold focus:outline-none";
  const variantStyles = {
    primary: "bg-blue-500 text-white hover:bg-blue-600",
    outline: "border-2 border-blue-500 text-blue-500 hover:bg-blue-100",
  };

  return (
    <button
      onClick={onClick}
      className={`${baseStyles} ${variantStyles[variant]} ${className}`}
    >
      {children}
    </button>
  );
};
