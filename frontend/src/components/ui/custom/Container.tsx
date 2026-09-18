import React from "react";

interface ContainerProps {
  children: React.ReactNode;
  className?: String;
}

export default function Container({ children, className }: ContainerProps) {
  return (
    <div
      className={`mx-auto  flex flex-col gap-1  items-center  w-full 
   sm:max-w-md  md:max-w-5xl xl:max-w-7xl  ${className}  `}
    >
      {children}
    </div>
  );
}
