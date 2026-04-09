declare module 'react-native/Libraries/Types/CodegenTypes' {
  export type DirectEventHandler<T> = (event: { nativeEvent: T }) => void;
  export type BubblingEventHandler<T> = (event: { nativeEvent: T }) => void;
  
  export type Double = number;
  export type Float = number;
  export type Int32 = number;
  export type WithDefault<Type, Value> = Type | undefined | null;
}
